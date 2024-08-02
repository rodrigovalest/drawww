import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InputComponent } from './input.component';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

describe('InputComponent', () => {
  let component: InputComponent;
  let fixture: ComponentFixture<InputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, InputComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InputComponent);
    component = fixture.componentInstance;
    component.control = new FormControl('', { nonNullable: true });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should bind the attribute "class" in outer div class on template', async () => {
    const mockedInputClass = 'test-class';
    fixture.componentRef.setInput('class', mockedInputClass);
    fixture.detectChanges();

    await fixture.whenStable();

    const div: HTMLElement = fixture.nativeElement.querySelector('div');
    expect(div.classList).toContain(mockedInputClass);
  });

  it('should bind "text" in template', async () => {
    const mockedInputText = 'test text';
    fixture.componentRef.setInput('text', mockedInputText);
    fixture.detectChanges();

    await fixture.whenStable();

    const p: HTMLElement = fixture.nativeElement.querySelector('p');
    expect(p.textContent).toBe(mockedInputText);
  });

  describe('input type', () => {
    it('should set "type" with "text" value and apply it to the input tag on template', async () => {
      const mockedInputType = 'text';
      fixture.componentRef.setInput('type', mockedInputType);
      fixture.detectChanges();

      await fixture.whenStable();

      const input = fixture.nativeElement.querySelector('input') as HTMLInputElement;
      expect(input.type).toBe(mockedInputType);
    });

    it('should set "type" with "password" value and apply it to the input tag on template', async () => {
      const mockedInputType = 'password';
      fixture.componentRef.setInput('type', mockedInputType);
      fixture.detectChanges();

      await fixture.whenStable();

      const input = fixture.nativeElement.querySelector('input') as HTMLInputElement;
      expect(input.type).toBe(mockedInputType);
    });

    it('should default to "text" for invalid input types', async () => {
      const mockedInputType = 'invalidType';
      fixture.componentRef.setInput('type', mockedInputType);
      fixture.detectChanges();

      await fixture.whenStable();

      const input = fixture.nativeElement.querySelector('input') as HTMLInputElement;
      expect(input.type).toBe('text');
    });
  });

  it('should input "control" and bind FormControl to input', async () => {
    const control = new FormControl('initial value', { nonNullable: true });
    fixture.componentRef.setInput('control', control);
    fixture.detectChanges();

    await fixture.whenStable();

    const input = fixture.nativeElement.querySelector('input') as HTMLInputElement;
    expect(input.value).toBe('initial value');

    input.value = 'new value';
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    await fixture.whenStable();

    expect(control.value).toBe('new value');
  });
});
