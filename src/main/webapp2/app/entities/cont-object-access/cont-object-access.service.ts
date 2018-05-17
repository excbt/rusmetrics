import { ContObjectAccess } from './cont-object-access.model';
import { Injectable } from '@angular/core';
import { ExcAbstractService } from '../../shared-blocks/exc-tools/exc-abstract-service';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class ContObjectAccessService extends ExcAbstractService<ContObjectAccess> {
    constructor(http: HttpClient) {
        super({apiUrl: 'api/subscr-access/cont-objects/'}, http);
     }
}
