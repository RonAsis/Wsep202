import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnedStoresComponent } from './owned-stores.component';

describe('OwnedStoresComponent', () => {
  let component: OwnedStoresComponent;
  let fixture: ComponentFixture<OwnedStoresComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OwnedStoresComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnedStoresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
