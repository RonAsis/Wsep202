import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceiptElementComponent } from './receipt-element.component';

describe('RecipetElementComponent', () => {
  let component: ReceiptElementComponent;
  let fixture: ComponentFixture<ReceiptElementComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReceiptElementComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceiptElementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
