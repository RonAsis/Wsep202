export class DailyVistorDto {
  constructor(public date: Date,
              public guests: number,
              public ownerStores: number,
              public managerStores: number,
              public simpleUser: number,
              public admins: number) {
  }
}
