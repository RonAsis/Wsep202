import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductInReceiptComponent } from './product-in-receipt.component';

describe('ProductInReceiptComponent', () => {
  let component: ProductInReceiptComponent;
  let fixture: ComponentFixture<ProductInReceiptComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProductInReceiptComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProductInReceiptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
