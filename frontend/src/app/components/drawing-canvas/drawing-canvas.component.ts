import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import * as fabric from 'fabric';

@Component({
  selector: 'app-drawing-canvas',
  standalone: true,
  imports: [],
  templateUrl: './drawing-canvas.component.html'
})
export class DrawingCanvasComponent implements OnInit {
  
  @ViewChild('canvas', { static: true }) canvasElement!: ElementRef<HTMLCanvasElement>;
  private canvas!: fabric.Canvas;
  private isDrawing: boolean = false;

  ngOnInit(): void {
    this.canvas = new fabric.Canvas(this.canvasElement.nativeElement, {
      isDrawingMode: true,
    });

    console.log(this.canvas);
    console.log(this.canvas.freeDrawingBrush);

    this.canvas.freeDrawingBrush = new fabric.PencilBrush(this.canvas);
    this.canvas.freeDrawingBrush.width = 5;
    this.canvas.freeDrawingBrush.color = '#000000';

    this.canvas.on('mouse:down', e => {
      this.isDrawing = true;
      const pointer = this.canvas.getScenePoint(e.e);
      this.canvas.freeDrawingBrush?.onMouseDown(pointer, e);
      // console.log(e);
    });

    this.canvas.on('mouse:up', e => {
      this.isDrawing = false;
      this.canvas.freeDrawingBrush?.onMouseUp(e);
    });

    this.canvas.on('mouse:move', e => {
      if (this.isDrawing) {
        const pointer = this.canvas.getScenePoint(e.e);
        this.canvas.freeDrawingBrush?.onMouseMove(pointer, e);
        // console.log(e);
      }
    });

    this.canvas.on('path:created', (e) => {
      // console.log('Desenho criado:', e);
    });

    this.canvas.renderAll();
  }

  getSVG(): string {
    return this.canvas.toSVG();
  }
}
