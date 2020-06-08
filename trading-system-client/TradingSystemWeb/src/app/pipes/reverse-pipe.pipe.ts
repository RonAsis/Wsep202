import { Pipe, PipeTransform } from '@angular/core';
import {filter, reverse} from 'lodash';
import {NotificationDto} from '../shared/notification.model';

@Pipe({
  name: 'reverse'
})
export class ReversePipe implements PipeTransform {

  transform(value: NotificationDto): NotificationDto[] {
    if (!value) { return []; }

    value = filter(value, ['dismissed', false]);
    return reverse(value);
  }

}
