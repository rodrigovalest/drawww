import { Component, Input } from '@angular/core';
import { UserStatusType } from '../../types/user-status.type';

@Component({
  selector: 'app-user-icon',
  standalone: true,
  imports: [],
  templateUrl: './user-icon.component.html'
})
export class UserIconComponent {
  @Input() class: string = '';
  @Input({ required: true }) status!: UserStatusType;
}
