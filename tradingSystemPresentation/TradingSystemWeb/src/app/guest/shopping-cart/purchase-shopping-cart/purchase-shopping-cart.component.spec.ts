import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PurchaseShoppingCartComponent } from './purchase-shopping-cart.component';

describe('PurchaseShoppingCartComponent', () => {
  let component: PurchaseShoppingCartComponent;
  let fixture: ComponentFixture<PurchaseShoppingCartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PurchaseShoppingCartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PurchaseShoppingCartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
