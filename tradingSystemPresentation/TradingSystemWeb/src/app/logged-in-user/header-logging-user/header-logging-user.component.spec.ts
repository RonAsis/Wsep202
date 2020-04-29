import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderLoggingUserComponent } from './header-logging-user.component';

describe('HeaderLoggingUserComponent', () => {
  let component: HeaderLoggingUserComponent;
  let fixture: ComponentFixture<HeaderLoggingUserComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HeaderLoggingUserComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderLoggingUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
