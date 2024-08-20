import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-user-icon',
  standalone: true,
  imports: [],
  templateUrl: './user-icon.component.html'
})
export class UserIconComponent {
  @Input() class: string = '';
}
