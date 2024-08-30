import { Component, OnInit } from '@angular/core';
import { ButtonComponent } from '../button/button.component';
import { TitleComponent } from '../title/title.component';
import { UserIconComponent } from '../user-icon/user-icon.component';
import { RxStompService } from '../../services/rx-stomp.service';
import { IUserWaiting } from '../../interfaces/user-waiting.interface';
import { BackIconComponent } from "../back-icon/back-icon.component";

@Component({
  selector: 'app-waiting',
  standalone: true,
  imports: [TitleComponent, ButtonComponent, UserIconComponent, BackIconComponent],
  templateUrl: './waiting.component.html',
})
export class WaitingComponent implements OnInit {
  roomId: string = '';
  users: IUserWaiting[] = [];

  constructor(private rxStompService: RxStompService) {}

  ngOnInit(): void {
    this.rxStompService.getMessages$().subscribe(response => {
      console.log(response);
    
      if (response)
        console.log('CREATE PRIVATE ROOM (checking server response): ', response);

        switch (response.roomStatus) {
          case 'WAITING':
            console.log('waiting component: ', response);
            this.users = response.data.users;
            this.roomId = response.data.roomId;
            console.log(this.users);
            break;
          case 'PLAYING':
            break;
          case 'ERROR':
            alert('ERROR in waiting: ' + response.message);
            break;
          default:
            console.error("Unexpected message: ", response);
            break;
        }
    });
  }
  
  onReady() {
    this.rxStompService.changeUserStatus();
  }
}
