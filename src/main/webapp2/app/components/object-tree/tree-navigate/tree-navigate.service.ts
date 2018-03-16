import 'rxjs/add/operator/toPromise';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { TreeNode } from 'primeng/api';

@Injectable()
export class TreeNavigateService {

    constructor(private http: HttpClient) {}

    getTree() {
        return this.http.get<any>('http://192.168.84.239:8089/testTree.json')
            .toPromise()
            .then((res) => <TreeNode[]> res.data);
    }
}
