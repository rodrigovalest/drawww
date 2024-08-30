import { Component } from '@angular/core';
import { BackIconComponent } from "../back-icon/back-icon.component";

@Component({
  selector: 'app-result',
  standalone: true,
  imports: [BackIconComponent],
  templateUrl: './result.component.html'
})
export class ResultComponent {
  users = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
}
