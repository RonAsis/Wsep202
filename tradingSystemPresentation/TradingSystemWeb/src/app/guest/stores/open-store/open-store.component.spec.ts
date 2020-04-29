import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OpenStoreComponent } from './open-store.component';

describe('OpenStoreComponent', () => {
  let component: OpenStoreComponent;
  let fixture: ComponentFixture<OpenStoreComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OpenStoreComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OpenStoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
