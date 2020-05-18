import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AmountProductsComponent } from './amount-products.component';

describe('AmountProductsComponent', () => {
  let component: AmountProductsComponent;
  let fixture: ComponentFixture<AmountProductsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AmountProductsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AmountProductsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
