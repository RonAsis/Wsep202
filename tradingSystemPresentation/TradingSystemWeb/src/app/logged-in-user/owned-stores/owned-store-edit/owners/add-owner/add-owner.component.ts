import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {StoreService} from '../../../../../services/store.service';
import {Store} from '../../../../../shared/store.model';
import {Product} from '../../../../../shared/product.model';

@Component({
  selector: 'app-add-owner',
  templateUrl: './add-owner.component.html',
  styleUrls: ['./add-owner.component.css']
})
export class AddOwnerComponent implements OnInit {
  @Output() ownerItemAdded = new EventEmitter<string>();
  @Input() store: Store;
  usernames: string[];
  selectedNewOwner: string;
  messageColor: string;
  message: string;
  constructor(private storeService: StoreService) { }

  ngOnInit(): void {
    this.init();
  }

  private init() {
    this.storeService.getAllUsernameNotOwnerNotManger(this.store.storeId)
      .subscribe(usernames => {
        this.usernames = usernames;
        this.selectedNewOwner = usernames.length > 0 ? usernames[0] : '';
      });
  }

  onSelectNewOwner(username: string) {
    this.selectedNewOwner = username;
  }

  onAddOwner() {
    if (!(this.selectedNewOwner.length === 0)) {
      this.storeService.addOwner(this.store.storeId, this.selectedNewOwner)
        .subscribe(response => {
          if (response) {
            this.ownerItemAdded.emit(this.selectedNewOwner);
            this.init();
            this.sucMessage();
          } else {
            this.errorMessage('Cant add user to be owner');
          }
        });
    }else{
      this.errorMessage('You must select User');
    }
  }
  errorMessage(message: string){
    this.message = message;
    this.messageColor = 'red';
  }

  sucMessage(){
    this.message = 'the owner is added';
    this.messageColor = 'blue';
  }
}
