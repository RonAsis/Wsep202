import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConditionalDiscountComponent } from './conditional-discount.component';

describe('ConditionalDiscountComponent', () => {
  let component: ConditionalDiscountComponent;
  let fixture: ComponentFixture<ConditionalDiscountComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConditionalDiscountComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConditionalDiscountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
