import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ButtonComponent } from '../button/button.component';
import { InputErrorComponent } from '../input-error/input-error.component';
import { InputComponent } from '../input/input.component';
import { TitleComponent } from '../title/title.component';
import { RxStompService } from '../../services/rx-stomp.service';
import { IRoomEnter } from '../../interfaces/room-enter.interface';
import { Router } from '@angular/router';
import { BackIconComponent } from "../back-icon/back-icon.component";

@Component({
  selector: 'app-enter-in-private-room',
  standalone: true,
  imports: [TitleComponent, InputErrorComponent, InputComponent, ButtonComponent, BackIconComponent],
  templateUrl: './enter-in-private-room.component.html'
})
export class EnterInPrivateRoomComponent implements OnInit {
  
  roomForm: FormGroup<{ 
    roomId: FormControl<string>; 
    roomPassword: FormControl<string>; 
  }>;

  constructor(private rxStompService: RxStompService, private router: Router) {
    this.roomForm = new FormGroup({
      roomId: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
      roomPassword: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    });
  }

  ngOnInit() {
    this.rxStompService.disconnect()

    this.rxStompService.getGameState$().subscribe(state => {
      console.log('ENTER IN ROOM GAME STATE: ', state);
    });
  }
  
  onEnter() {
    if (!this.roomForm.valid) 
      return;

    const roomEnterData: IRoomEnter = this.roomForm.getRawValue();
    const username = localStorage.getItem('username');
    const password = localStorage.getItem('password');

    this.rxStompService.connect(username || '', password || '');

    this.rxStompService.enterPrivateRoom(roomEnterData);

    this.rxStompService.getMessages$().subscribe(response => {
      console.log('ENTER IN PRIVATE ROOM (checking server response): ', response);

        switch (response.roomStatus) {
          case 'WAITING':
            this.router.navigate(['/private/game']);
            break;
          case 'ERROR':
            alert('Failed to enter in room: ' + response.message);
            this.rxStompService.disconnect();
            break;
          default:
            console.error("Unexpected message: ", response);
            break;
        }
    });
  }
}
