import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-title',
  standalone: true,
  imports: [],
  templateUrl: './title.component.html',
})
export class TitleComponent {
  @Input() class: string = '';
  @Input({ required: true }) title!: string;
}
