import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import * as fabric from 'fabric';
import { ToolType } from '../../types/tool.type';

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
  currentColor: string = '#000000';

  private undoStack: string[] = [];
  private redoStack: string[] = [];

  ngOnInit(): void {
    this.canvas = new fabric.Canvas(this.canvasElement.nativeElement, {
      isDrawingMode: true,
    });

    this.onPencil();

    this.canvas.on('mouse:down', e => {
      this.isDrawing = true;
      const pointer = this.canvas.getScenePoint(e.e);
      this.canvas.freeDrawingBrush?.onMouseDown(pointer, e);
      this.saveState();
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
    this.currentTool = 'PENCIL';
    this.canvas.freeDrawingBrush = new fabric.PencilBrush(this.canvas);
    this.canvas.freeDrawingBrush.width = this.currenPencilWidth;
    this.canvas.freeDrawingBrush.color = this.currentColor;
  }

  onEraser() {
    this.canvas.freeDrawingBrush = new fabric.PencilBrush(this.canvas);
    this.canvas.freeDrawingBrush.width = 65;
    this.canvas.freeDrawingBrush.color = '#f8f8f8';
    this.currentTool = 'ERASER';
  }

  onWidthChange(width: number) {
    this.currenPencilWidth = width;

    if (this.canvas.freeDrawingBrush)
      this.canvas.freeDrawingBrush.width = this.currenPencilWidth;
  }

  onColorSelect(color: string) {
    this.currentColor = color;

    if (this.canvas.freeDrawingBrush)
      this.canvas.freeDrawingBrush.color = this.currentColor;
  }

  saveState() {
    this.undoStack.push(JSON.stringify(this.canvas.toJSON()));
    this.redoStack = [];
  }

  onUndo() {
    if (this.undoStack.length > 0) {
      this.redoStack.push(JSON.stringify(this.canvas.toObject()));
      const lastState = this.undoStack.pop();

      if (lastState) {
        this.canvas.clear();
        this.canvas.loadFromJSON(lastState);
      }
    }
  }

  onRedo() {
    if (this.redoStack.length > 0) {
      this.undoStack.push(JSON.stringify(this.canvas.toObject()));
      const lastState = this.redoStack.pop();

      if (lastState) {
        this.canvas.clear();
        this.canvas.loadFromJSON(lastState);
      }
    }
  }
}
