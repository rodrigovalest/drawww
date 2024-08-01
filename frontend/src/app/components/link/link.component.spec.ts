import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LinkComponent } from './link.component';
import { provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

describe('LinkComponent', () => {
  let component: LinkComponent;
  let fixture: ComponentFixture<LinkComponent>;
  let router: Router;
  let harness: RouterTestingHarness;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LinkComponent],
      providers: [
        provideRouter([{ path: '**', component: LinkComponent }]),
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LinkComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    harness = await RouterTestingHarness.create();
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

    const button: HTMLElement = fixture.nativeElement.querySelector('a');
    expect(button.textContent).toContain(mockedInputText);
  });

  it('should bind "path" to routerLink', async () => {
    const mockedInputPath = 'login';
    fixture.componentRef.setInput('path', mockedInputPath);
    fixture.detectChanges();

    await fixture.whenStable();

    const anchor: HTMLAnchorElement = fixture.nativeElement.querySelector('a');
    expect(anchor.getAttribute('ng-reflect-router-link')).toBe('/' + mockedInputPath);
  });

  it('should navigate when link is clicked', async () => {
    const mockedInputPath = 'login';
    fixture.componentRef.setInput('path', mockedInputPath);
    fixture.detectChanges();

    await fixture.whenStable();

    const anchor: HTMLAnchorElement = fixture.nativeElement.querySelector('a');
    anchor.click();
    fixture.detectChanges();

    await fixture.whenStable();

    expect(router.url).toBe('/' + mockedInputPath);
  });
});
