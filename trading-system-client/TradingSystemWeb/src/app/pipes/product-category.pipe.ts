import { Pipe, PipeTransform } from '@angular/core';
import {Product} from '../shared/product.model';

@Pipe({
  name: 'productCategory'
})
export class ProductCategoryPipe implements PipeTransform {

  transform(products: Product[], category: string): Product[] {
    if (!products || category === 'All') {
      return products;
    }
    // filter items array, items which match and return true will be
    // kept, false will be filtered out
    return products.filter(product => product.category === category);
  }

}
