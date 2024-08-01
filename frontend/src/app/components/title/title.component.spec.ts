import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TitleComponent } from './title.component';

fdescribe('TitleComponent', () => {
  let component: TitleComponent;
  let fixture: ComponentFixture<TitleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TitleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TitleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should bind "text" in template', async () => {
    const mockedInputTitle = 'test-title';
    fixture.componentRef.setInput('title', mockedInputTitle);
    fixture.detectChanges();

    await fixture.whenStable();

    const button: HTMLElement = fixture.nativeElement.querySelector('h2');
    expect(button.textContent).toContain(mockedInputTitle);
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
