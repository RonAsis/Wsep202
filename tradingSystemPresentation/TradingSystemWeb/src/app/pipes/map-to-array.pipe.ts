import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'mapToArray'
})
export class MapToArrayPipe implements PipeTransform {

  transform(map: Map<any, any>): any[] {
    const ret = [];

    map.forEach((val, key) => {
      ret.push({k:
        key,
        v: val
      });
    });
    return ret;
  }

}
