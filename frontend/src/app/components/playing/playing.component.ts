import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { DrawingCanvasComponent } from "../drawing-canvas/drawing-canvas.component";
import { RxStompService } from '../../services/rx-stomp.service';
import { interval, Subscription } from 'rxjs';
import { DrawingService } from '../../services/drawing.service';
import { HttpErrorResponse } from '@angular/common/http';
import { LoadingComponent } from "../loading/loading.component";

@Component({
  selector: 'app-playing',
  standalone: true,
  imports: [DrawingCanvasComponent, LoadingComponent],
  templateUrl: './playing.component.html'
})
export class PlayingComponent implements OnInit, OnDestroy {
  private startTime: Date | null = null;
  private endTime: Date | null = null;
  time: string = '00:00';
  theme: string = '';
  private intervalSubscription: Subscription | null = null;
  isLoading: boolean = false;

  @ViewChild(DrawingCanvasComponent) drawingCanvasComponent!: DrawingCanvasComponent;

  constructor(private rxStompService: RxStompService, private drawingService: DrawingService) {}

  ngOnInit(): void {
    this.rxStompService.getMessages$().subscribe(response => {
      console.log(response);
    
      if (response) {
        console.log('PLAYING (checking server response): ', response);
      
        switch (response.roomStatus) {
          case 'PLAYING':
            this.theme = response.data.theme;
            this.startTime = new Date(response.data.startTime);
            this.endTime = new Date(response.data.endTime);
            console.log('start time: ', this.startTime);
            console.log('end time: ', this.endTime);
            this.updateTime();
            break;
          case 'ERROR':
            console.error('ERROR in playing: ', response.message);
            break;
          default:
            console.error("Unexpected message: ", response);
            break;
        }
      }
    });

    this.intervalSubscription = interval(1000).subscribe(() => this.updateTime());
  }

  ngOnDestroy(): void {
    if (this.intervalSubscription)
      this.intervalSubscription.unsubscribe();
  }

  private updateTime(): void {
    if (this.startTime && this.endTime) {
      const now = new Date();
      const timeLeft = Math.max(0, this.endTime.getTime() - now.getTime());
      this.time = this.formatTime(timeLeft);

      if (timeLeft === 0) {
        this.isLoading = true;
        this.sendUserDraw();

        if (this.intervalSubscription)
          this.intervalSubscription.unsubscribe();
      }
    }
  }

  private formatTime(milliseconds: number): string {
    const totalSeconds = Math.floor(milliseconds / 1000);
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    const formatedMinutes = minutes < 10 ? '0' + minutes : minutes.toString();
    const formatedSeconds = seconds < 10 ? '0' + seconds : seconds.toString();
    return `${formatedMinutes}:${formatedSeconds}`;
  }

  private sendUserDraw() {
    const svgDraw: string = this.drawingCanvasComponent.getSVG();

    this.drawingService.sendUserDraw({ svgDraw: svgDraw }).subscribe({
      next: (data) => {
        console.log(data);
      },
      error: (httpError: HttpErrorResponse) => console.log(httpError)
    });
  }
}
