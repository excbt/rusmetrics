import { Notice } from './notice.model';

export class NoticeWrapperPaged {
    constructor(public objects: Notice[],
                public totalElements: number,
                public totalPages: number) {}
}