import { TestBed } from '@angular/core/testing';

import { RxStompService } from './rx-stomp.service';

describe('RxStompServiceService', () => {
  let service: RxStompService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RxStompService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
