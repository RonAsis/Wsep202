import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Store} from '../../../../../shared/store.model';
import {StoreService} from '../../../../../services/store.service';
import {Manager} from '../../../../../shared/manager.model';

@Component({
  selector: 'app-add-manager',
  templateUrl: './add-manager.component.html',
  styleUrls: ['./add-manager.component.css']
})
export class AddManagerComponent implements OnInit {

  @Output() managerItemAdded = new EventEmitter<Manager>();
  @Input() store: Store;
  usernames: string[];
  selectedNewManager: string;
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
        this.selectedNewManager = usernames.length > 0 ? usernames[0] : '';
      });
  }

  onSelectNewOwner(username: string) {
    this.selectedNewManager = username;
  }

  onAddManager() {
    if (!(this.selectedNewManager.length === 0)) {
      this.storeService.addManager(this.store.storeId, this.selectedNewManager)
        .subscribe(response => {
          if (response !== undefined && response !== null) {
            this.managerItemAdded.emit(response);
            this.init();
            this.sucMessage();
          } else {
            this.errorMessage('Cant add user to be manager');
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
    this.message = 'the manager is added';
    this.messageColor = 'blue';
  }
}
