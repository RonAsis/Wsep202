import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApproveOwnerComponent } from './approve-owner.component';

describe('ApproveOwnerComponent', () => {
  let component: ApproveOwnerComponent;
  let fixture: ComponentFixture<ApproveOwnerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApproveOwnerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApproveOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
