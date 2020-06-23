export class Product {
  constructor(public productSn: number,
              public name: string,
              public category: string,
              public amount: number,
              public cost: number,
              public rank: number,
              public storeId: number,
              public originalCost: number) {}
}
