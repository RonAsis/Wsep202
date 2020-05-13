export class ProductShoppingCartDto {
  constructor(public productSn: number,
              public name: string,
              public amountInShoppingCart: number,
              public cost: number,
              public originalCost: number,
              public storeId: number) {}
}
