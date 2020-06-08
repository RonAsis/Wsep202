import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagedStoresComponent } from './managed-stores.component';

describe('ManagedStoresComponent', () => {
  let component: ManagedStoresComponent;
  let fixture: ComponentFixture<ManagedStoresComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ManagedStoresComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ManagedStoresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
