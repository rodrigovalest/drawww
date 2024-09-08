import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import * as fabric from 'fabric';

export type ToolType = 'PENCIL' | 'ERASER' | 'TRIANGLE' | 'SQUARE' | 'CIRCLE' | 'LINE';

@Component({
  selector: 'app-drawing-canvas',
  standalone: true,
  imports: [],
  templateUrl: './drawing-canvas.component.html',
  styleUrl: './drawing-canvas.component.css'
})
export class DrawingCanvasComponent implements OnInit {
  
  @ViewChild('canvas', { static: true }) canvasElement!: ElementRef<HTMLCanvasElement>;
  private canvas!: fabric.Canvas;
  private isDrawing: boolean = false;
  currentTool: ToolType = 'PENCIL';
  currenPencilWidth: number = 3;

  ngOnInit(): void {
    this.canvas = new fabric.Canvas(this.canvasElement.nativeElement, {
      isDrawingMode: true,
    });

    this.onPencil();

    this.canvas.on('mouse:down', e => {
      this.isDrawing = true;
      const pointer = this.canvas.getScenePoint(e.e);
      this.canvas.freeDrawingBrush?.onMouseDown(pointer, e);
    });

    this.canvas.on('mouse:up', e => {
      this.isDrawing = false;
      this.canvas.freeDrawingBrush?.onMouseUp(e);
    });

    this.canvas.on('mouse:move', e => {
      if (this.isDrawing) {
        const pointer = this.canvas.getScenePoint(e.e);
        this.canvas.freeDrawingBrush?.onMouseMove(pointer, e);
      }
    });

    this.canvas.renderAll();
  }

  getSVG(): string {
    return this.canvas.toSVG();
  }

  onPencil() {
    console.log('on pencil')
    this.currentTool = 'PENCIL';
    this.canvas.freeDrawingBrush = new fabric.PencilBrush(this.canvas);
    this.canvas.freeDrawingBrush.width = this.currenPencilWidth;
    this.canvas.freeDrawingBrush.color = '#000000';
  }

  onEraser() {
    console.log('on eraser')
    this.canvas.freeDrawingBrush = new fabric.PencilBrush(this.canvas);
    this.canvas.freeDrawingBrush.width = 65;
    this.canvas.freeDrawingBrush.color = '#f8f8f8';
    this.currentTool = 'ERASER';
  }

  onUndo() {
    console.log('on undo')
  }

  onRedo() {
    console.log('on redo')
  }

  onWidthChange(width: number) {
    this.currenPencilWidth = width;

    if (this.canvas.freeDrawingBrush)
      this.canvas.freeDrawingBrush.width = this.currenPencilWidth;
  }
}
