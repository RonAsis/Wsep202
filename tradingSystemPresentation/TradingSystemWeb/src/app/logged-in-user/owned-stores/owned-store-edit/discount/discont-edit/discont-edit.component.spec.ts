import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DiscontEditComponent } from './discont-edit.component';

describe('DiscontEditComponent', () => {
  let component: DiscontEditComponent;
  let fixture: ComponentFixture<DiscontEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DiscontEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DiscontEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
