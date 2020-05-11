import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnerItemComponent } from './owner-item.component';

describe('OwnerItemComponent', () => {
  let component: OwnerItemComponent;
  let fixture: ComponentFixture<OwnerItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OwnerItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnerItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
