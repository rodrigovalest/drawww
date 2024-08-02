import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-link',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './link.component.html',
})
export class LinkComponent {
  @Input() class: string = '';
  @Input({ required: true }) path!: string;
  @Input({ required: true }) text!: string;
}
