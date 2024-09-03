import { Component, ElementRef, ViewChild } from '@angular/core';
import { RxStompService } from '../../services/rx-stomp.service';
import { CompressionService } from '../../services/compression.service';

@Component({
  selector: 'app-voting',
  standalone: true,
  imports: [],
  templateUrl: './voting.component.html'
})
export class VotingComponent {
  @ViewChild('canvas') canvas!: ElementRef<HTMLCanvasElement>;
  draw: string | null = null;
  theme: string = '';
  username: string = '';
  rate: string = '';

  constructor(private rxStompService: RxStompService) {}

  ngAfterViewInit(): void {
    this.rxStompService.getMessages$().subscribe(response => {
      console.log(response);
    
      if (response) {
        console.log('VOTING (checking server response): ', response);
      
        switch (response.roomStatus) {
          case 'VOTING':
            // const base64String = response.data.drawSvg;
            // const binaryString = atob(base64String);

            // const uint8Array = new Uint8Array(binaryString.length);
            // for (let i = 0; i < binaryString.length; i++)
            //   uint8Array[i] = binaryString.charCodeAt(i);

            // this.draw = CompressionService.decompressSVG(uint8Array);
            this.draw = response.data.svgDraw;
            console.log(this.draw);
            this.username = response.data.targetUsername;
            this.theme = response.data.theme;
            if (this.draw)
              this.renderSVG(this.draw);
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

  private renderSVG(svgContent: string): void {
    const canvasEl = this.canvas.nativeElement;
    const ctx = canvasEl.getContext('2d');

    if (ctx) {
      const img = new Image();
      img.onload = () => {
        ctx.clearRect(0, 0, canvasEl.width, canvasEl.height);
        ctx.drawImage(img, 0, 0, canvasEl.width, canvasEl.height);
      };
      img.src = 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svgContent);
    }
  }

  onRating(rate: number) {
    this.rxStompService.sendUserVote(rate);
  }
}
