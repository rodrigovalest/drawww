import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ButtonComponent } from './button.component';

fdescribe('ButtonComponent', () => {
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

  it('should bind "text" in template', async () => {
    const mockedInputText = 'test text';
    fixture.componentRef.setInput('text', mockedInputText);
    fixture.detectChanges();

    await fixture.whenStable();

    const button: HTMLElement = fixture.nativeElement.querySelector('button');
    expect(button.textContent).toContain(mockedInputText);
  });

  it('should emit event when click in button', () => {
    spyOn(component.buttonClicked, 'emit');

    const button: HTMLElement = fixture.nativeElement.querySelector('button');
    button.click();

    expect(component.buttonClicked.emit).toHaveBeenCalled();
  });

  it('should bind the attribute "class" in outer div class on template', async () => {
    const mockedInputClass = 'test-class';
    fixture.componentRef.setInput('class', mockedInputClass);
    fixture.detectChanges();

    await fixture.whenStable();

    const div: HTMLElement = fixture.nativeElement.querySelector('div');
    expect(div.classList).toContain(mockedInputClass);
  });
});
