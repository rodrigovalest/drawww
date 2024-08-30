import { Injectable } from '@angular/core';
import { RxStomp, RxStompConfig } from '@stomp/rx-stomp';
import { environment } from '../environments/environment';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { IRoomCreate } from '../interfaces/room-create.interface';
import { IRoomEnter } from '../interfaces/room-enter.interface';
import { IWebSocketResponse } from '../interfaces/websocket-response.interface';
import { GameStateType } from '../types/game-state.type';
import { ISendUserDraw } from '../interfaces/send-draw.interface';

@Injectable({
  providedIn: 'root'
})
export class RxStompService extends RxStomp {

  private messageSubject: BehaviorSubject<IWebSocketResponse | null> = new BehaviorSubject<IWebSocketResponse | null>(null);
  private gameStateSubject: BehaviorSubject<GameStateType> = new BehaviorSubject<GameStateType>('NOT_STARTED');

  constructor() {
    super();
  }

  connect(username: string, password: string): void {
    this.disconnect();

    const config: RxStompConfig = {
      brokerURL: `${environment.websocketApiUrl}/ws`,
      heartbeatIncoming: 0,
      heartbeatOutgoing: 20000,
      reconnectDelay: 1000000000,
      connectHeaders: {
        login: username,
        passcode: password,
      },
      debug: msg => {
        // console.log(msg)
      }
    };
    this.configure(config);
    this.activate();

    this.watch('/user/queue/reply').subscribe(message => {
      const data: IWebSocketResponse = JSON.parse(message.body);
      console.log("subscribe in RxStompService: ", data);

      if (data.roomStatus !== 'ERROR')
        this.gameStateSubject.next(data.roomStatus);
      
      this.messageSubject.next(JSON.parse(message.body));
    });
  }

  disconnect(): void {
    this.messageSubject.next(null);
    this.gameStateSubject.next('NOT_STARTED');
    this.deactivate();
  }

  isConnected(): boolean {
    return this.connected();
  }

  getMessages$(): Observable<any> {
    return this.messageSubject.asObservable();
  }

  getGameState$(): Observable<GameStateType> {
    return this.gameStateSubject.asObservable();
  }

  createPrivateRoom(createRoomData: IRoomCreate) {
    this.publish({ destination: '/app/rooms/private/create', body: JSON.stringify(createRoomData) });
  }

  enterPrivateRoom(enterRoomData: IRoomEnter) {
    this.publish({ destination: '/app/rooms/private/enter', body: JSON.stringify(enterRoomData) });
  }

  leaveRoom() {
    this.publish({ destination: '/app/rooms/leave' });
  }

  changeUserStatus() {
    this.publish({ destination: '/app/rooms/user_status' });
  }

  sendUserDraw(binaryDraw: Uint8Array) {
    this.publish({ destination: '/app/rooms/send_draw',  binaryBody: binaryDraw });
  }

  sendUserVote(rate: number) {
    this.publish({ destination: '/app/rooms/send_vote',  body: JSON.stringify({ rate: rate })  });
  }
}
