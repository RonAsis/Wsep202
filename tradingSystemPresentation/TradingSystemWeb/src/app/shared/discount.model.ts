import {Product} from './product.model';

export class Discount {
  constructor(public discountId: number,
              public discountPercentage: number,
              public endTime: Date,
              public productsUnderThisDiscount: Product[],
              public description: string,
              public amountOfProductsForApplyDiscounts: Product[],
              public minPrice: number,
              public composedDiscounts: Discount[],
              public compositeOperator: string) {}
}
