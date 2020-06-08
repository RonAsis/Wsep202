import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductEditItemComponent } from './product-edit-item.component';

describe('ProductEditItemComponent', () => {
  let component: ProductEditItemComponent;
  let fixture: ComponentFixture<ProductEditItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProductEditItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProductEditItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
