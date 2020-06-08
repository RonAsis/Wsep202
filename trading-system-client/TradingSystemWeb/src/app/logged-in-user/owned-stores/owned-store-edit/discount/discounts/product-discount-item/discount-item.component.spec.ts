import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DiscountItemComponent } from './discount-item.component';

describe('ProductDiscountItemComponent', () => {
  let component: DiscountItemComponent;
  let fixture: ComponentFixture<DiscountItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DiscountItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DiscountItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
