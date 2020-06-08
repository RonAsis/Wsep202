import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VisitorDailyComponent } from './visitor-daily.component';

describe('VisitorDailyComponent', () => {
  let component: VisitorDailyComponent;
  let fixture: ComponentFixture<VisitorDailyComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VisitorDailyComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VisitorDailyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
