import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ButtonComponent } from '../../components/button/button.component';
import { InputErrorComponent } from '../../components/input-error/input-error.component';
import { InputComponent } from '../../components/input/input.component';
import { TitleComponent } from '../../components/title/title.component';
import { IRoomCreate } from '../../interfaces/room-create.interface';
import { RxStompService } from '../../services/rx-stomp.service';
import { Router } from '@angular/router';
import { BackIconComponent } from "../../components/back-icon/back-icon.component";

@Component({
  selector: 'app-create-private-room',
  standalone: true,
  imports: [TitleComponent, InputComponent, ButtonComponent, InputErrorComponent, BackIconComponent],
  templateUrl: './create-private-room.component.html',
})
export class CreatePrivateRoomComponent implements OnInit {

  roomForm: FormGroup<{ 
    roomPassword: FormControl<string>; 
  }>;

  constructor(private rxStompService: RxStompService, private router: Router) {
    this.roomForm = new FormGroup({
      roomPassword: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    });
  }

  ngOnInit() {
    this.rxStompService.disconnect()

    this.rxStompService.getGameState$().subscribe(state => {
      console.log('CREATE PRIVATE ROOM GAME STATE: ', state);
    });
  }

  onCreate() {
    if (!this.roomForm.valid) return;

    const roomCreateData: IRoomCreate = this.roomForm.getRawValue();
    const username = localStorage.getItem('username');
    const password = localStorage.getItem('password');

    this.rxStompService.connect(username || '', password || '');

    this.rxStompService.createPrivateRoom(roomCreateData);

    this.rxStompService.getMessages$().subscribe(response => {
      if (response)
        console.log('CREATE PRIVATE ROOM (checking server response): ', response);

        switch (response.roomStatus) {
          case 'WAITING':
            this.router.navigate(['/private/game']);
            break;
          case 'ERROR':
            alert('Failed to create room: ' + response.message);
            this.rxStompService.disconnect();
            break;
          default:
            console.error("Unexpected message: ", response);
            break;
        }
    });
  }
}
