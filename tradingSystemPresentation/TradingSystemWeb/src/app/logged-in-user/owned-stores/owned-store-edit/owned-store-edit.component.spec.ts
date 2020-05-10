import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnedStoreEditComponent } from './owned-store-edit.component';

describe('OwnedStoreEditComponent', () => {
  let component: OwnedStoreEditComponent;
  let fixture: ComponentFixture<OwnedStoreEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OwnedStoreEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnedStoreEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
