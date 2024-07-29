import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ButtonComponent } from './button.component';

describe('ButtonComponent', () => {
  let component: ButtonComponent;
  let fixture: ComponentFixture<ButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ButtonComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should input string "class"', () => {
    const testClass = 'test-class';
    component.class = testClass;
    fixture.detectChanges();
    
    const divElement = fixture.debugElement.query(By.css('div')).nativeElement;
    expect(divElement.className).toContain(testClass);
  });

  it('should input string "text"', () => {
    const testText = 'Test Button';
    component.text = testText;
    fixture.detectChanges();

    const buttonElement = fixture.debugElement.query(By.css('button')).nativeElement;
    expect(buttonElement.textContent).toContain(testText);
  });

  it('should emit "buttonClicked" event on button click', () => {
    spyOn(component.buttonClicked, 'emit');
    
    const buttonElement = fixture.debugElement.query(By.css('button')).nativeElement;
    buttonElement.click();


    expect(component.buttonClicked.emit).toHaveBeenCalled();
  });

  it('should apply class from "class" input to the outer div', () => {
    const testClass = 'test-class';
    component.class = testClass;
    fixture.detectChanges();

    const divElement = fixture.debugElement.query(By.css('div')).nativeElement;
    expect(divElement.className).toContain(testClass);
  });
});
