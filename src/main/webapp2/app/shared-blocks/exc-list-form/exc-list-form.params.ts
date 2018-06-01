import { ExcAbstractPageDataSource } from '..';

export interface ExcListDatasourceProvider<T> {
    getDataSource: () => ExcAbstractPageDataSource<T>;
}

export interface ExcListFormParams {
    modificationEventName?: string;
    onSaveUrl?: string;
    onDeleteUrl?: string;
}
