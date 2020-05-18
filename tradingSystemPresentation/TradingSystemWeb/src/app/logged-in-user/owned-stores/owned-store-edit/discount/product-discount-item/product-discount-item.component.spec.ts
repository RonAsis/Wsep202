import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductDiscountItemComponent } from './product-discount-item.component';

describe('ProductDiscountItemComponent', () => {
  let component: ProductDiscountItemComponent;
  let fixture: ComponentFixture<ProductDiscountItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProductDiscountItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProductDiscountItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
