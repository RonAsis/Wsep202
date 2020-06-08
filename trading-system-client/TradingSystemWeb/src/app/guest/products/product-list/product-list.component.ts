import {AfterViewInit, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {Product} from '../../../shared/product.model';
import {ProductService} from '../../../services/product.service';
import {LabelType, Options} from 'ng5-slider';
import {Store} from '../../../shared/store.model';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit, OnChanges {
  @Output() productWasSelected = new EventEmitter<Product>();
  products: Product[];
  @Input() store: Store;
  searchText;


  // for filter by product range
  productRank = 1;
  optionsRank: Options = {
    showTicksValues: true,
    stepsArray: [
      {value: 1, legend: 'Very poor'},
      {value: 2},
      {value: 3, legend: 'Fair'},
      {value: 4},
      {value: 5, legend: 'Average'},
      {value: 6},
      {value: 7, legend: 'Good'},
      {value: 8},
      {value: 9, legend: 'Excellent'}
    ]
  };

  // for filter by range price
  minValue = 0;
  maxValue = 100000;
  optionsRangePrice: Options = {
    floor: 0,
    ceil: 100000,
    translate: (value: number, label: LabelType): string => {
      switch (label) {
        case LabelType.Low:
          return '<b>Min price:</b> $' + value;
        case LabelType.High:
          return '<b>Max price:</b> $' + value;
        default:
          return '$' + value;
      }
    }
  };

  selectedCategory = 'All';
  categories: string [] = [this.selectedCategory];

  constructor(private productService: ProductService) {
    this.productService.getCategories().subscribe(categories => {
        if (categories !== null && categories !== undefined) {
          this.categories = this.categories.concat(categories);
        }
      }
    );
  }

  ngOnInit(): void {
    this.productService.filterByPriceEvent
      .subscribe(
        (range: { min: number, max: number }) => {
          this.products = this.productService.filterByPrice(range);
        });
    if (this.store !== null && this.store !== undefined) {
      this.products = this.store.products;
    } else {
      this.productService.getProducts().subscribe(products => {
        if (products !== null && products !== undefined) {
          this.products = products;
        }
      });
    }
  }

  onSelectCategory(category: string) {
    this.selectedCategory = category;
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.ngOnInit();
    console.log('iniital');
  }

}
