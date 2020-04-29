import {AfterViewInit, Component, Input, OnInit, ViewChild, ViewChildren} from '@angular/core';
import {Store} from '../../../shared/store.model';
import {ProductsComponent} from '../../products/products.component';
import {ProductListComponent} from '../../products/product-list/product-list.component';

@Component({
  selector: 'app-store-detail',
  templateUrl: './store-detail.component.html',
  styleUrls: ['./store-detail.component.css']
})
export class StoreDetailComponent implements OnInit, AfterViewInit{
  @Input()store: Store;

  constructor() { }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
  }
}