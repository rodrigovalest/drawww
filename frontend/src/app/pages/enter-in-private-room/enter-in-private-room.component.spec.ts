import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnterInPrivateRoomComponent } from './enter-in-private-room.component';

describe('EnterInPrivateRoomComponent', () => {
  let component: EnterInPrivateRoomComponent;
  let fixture: ComponentFixture<EnterInPrivateRoomComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnterInPrivateRoomComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EnterInPrivateRoomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
