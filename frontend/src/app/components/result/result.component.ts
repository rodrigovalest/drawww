import { Component, OnInit } from '@angular/core';
import { BackIconComponent } from "../back-icon/back-icon.component";
import { IUserRate } from '../../interfaces/user-vote.interface';
import { RxStompService } from '../../services/rx-stomp.service';

@Component({
  selector: 'app-result',
  standalone: true,
  imports: [BackIconComponent],
  templateUrl: './result.component.html'
})
export class ResultComponent implements OnInit {
  userRates: IUserRate[] = [];
  theme: string = '';

  constructor(private rxStompService: RxStompService) {}

  ngOnInit(): void {
    this.rxStompService.getMessages$().subscribe(response => {
      console.log(response);
    
      if (response) {
        console.log('RESULT (checking server response): ', response);
      
        switch (response.roomStatus) {
          case 'RESULT':
            this.theme = response.data.theme;
            this.userRates = response.data.rates;
            console.log(this.userRates);
            this.sortRates();
            break;
          case 'ERROR':
            alert('ERROR in playing: ' + response.message);
            break;
          default:
            console.error("Unexpected message: ", response);
            break;
        }
      }
    });
  }

  private sortRates() {
    this.userRates.sort((a, b) => b.averageRate - a.averageRate);

    this.userRates.forEach((userRate, index) => {
      userRate.position = `${index + 1}`;
    });
  }
}
