import { Component } from '@angular/core';
import { TitleComponent } from "../../components/title/title.component";
import { LinkComponent } from "../../components/link/link.component";

@Component({
  selector: 'app-page-not-found',
  standalone: true,
  imports: [TitleComponent, LinkComponent],
  templateUrl: './page-not-found.component.html',
})
export class PageNotFoundComponent {
}
