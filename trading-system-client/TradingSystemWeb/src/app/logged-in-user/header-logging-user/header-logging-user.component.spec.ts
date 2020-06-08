import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderLoggedInUserComponent } from './header-logging-user.component';

describe('HeaderLoggingUserComponent', () => {
  let component: HeaderLoggedInUserComponent;
  let fixture: ComponentFixture<HeaderLoggedInUserComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HeaderLoggedInUserComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderLoggedInUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
