export class Policy {
  constructor(public purchaseId: number,
              public purchaseType: string,
              public policyType: string,
              public description: string,
              public countriesPermitted: string[],
              public storeWorkDays: number[],
              public min: number,
              public max: number,
              public productSn: number,
              public compositeOperator: string,
              public composedPurchasePolicies: Policy[]) {}
}
