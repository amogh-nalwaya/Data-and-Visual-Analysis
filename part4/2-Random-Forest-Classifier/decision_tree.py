from util import entropy, information_gain, partition_classes
import numpy as np
import ast

class DecisionTree(object):
    def __init__(self):
        # Initializing the tree as an empty dictionary or list, as preferred
        #self.tree = []
        self.tree = {}

    def learn(self, X, y):
        # TODO: Train the decision tree (self.tree) using the the sample X and labels y
        # You will have to make use of the functions in utils.py to train the tree

        # One possible way of implementing the tree:
        #    Each node in self.tree could be in the form of a dictionary:
        #       https://docs.python.org/2/library/stdtypes.html#mapping-types-dict
        #    For example, a non-leaf node with two children can have a 'left' key and  a
        #    'right' key. You can add more keys which might help in classification
        #    (eg. split attribute and split value)

        self.tree = self.get_index(X, y)
        self.createDT(self.tree)

    def createDT(self, node):

        left_tree_x = node['left_tree_x']
        right_tree_x = node['right_tree_x']

        left_tree_y = node['left_tree_y']
        right_tree_y = node['right_tree_y']

        del(node['left_tree_x'])
        del(node['right_tree_x'])

        del(node['left_tree_y'])
        del(node['right_tree_y'])

        #Terminal case is when all y's in the leaf nodes are the same.

        if(self.is_Terminal(left_tree_y)) and (self.is_Terminal(right_tree_y)):
            node['left'] = left_tree_y[0]
            node['right'] = right_tree_y[0]
            return

        if(self.is_Terminal(left_tree_y)):
            node['left'] = left_tree_y[0]
        else:
            node['left'] = self.get_index(left_tree_x, left_tree_y)
            self.createDT(node['left'])

        if(self.is_Terminal(right_tree_y)):
            node['right'] = right_tree_y[0]
        else:
            node['right'] = self.get_index(right_tree_x, right_tree_y)
            self.createDT(node['right'])

    def get_index(self, X, y):
        split_index, split_val, X_left, X_right, y_left, y_right = None, None, None, None, None, None
        gain_compare = 0
        for i in range(len(X[0])):
            for row in X:
                X_l, X_r, y_l, y_r = partition_classes(X, y, i, row[i])
                gain = information_gain(y, [y_l, y_r])
                if gain > gain_compare:
                    split_index, split_val, gain_compare, X_left, X_right, y_left, y_right = i, row[i], gain, X_l, X_r, y_l, y_r

        return {'split_index':split_index, 'split_value':split_val, 'left_tree_x':X_left, 'left_tree_y': y_left, 'right_tree_x': X_right, 'right_tree_y': y_right}

    def classify(self, record):
        return self.classify_recursive(record, self.tree)

    def is_Terminal(self, s):
        return len(set(s)) == 1


    def classify_recursive(self, record, tree):
        if type(tree['split_value']) is str:
            if(record[tree['split_index']] == tree['split_value']):
                if(tree['left'] == 0) or (tree['left'] == 1):
                    return tree['left']
                else:
                    return self.classify_recursive(record, tree['left'])
            else:
                if(tree['right'] == 0) or (tree['right'] == 1):
                    return tree['right']
                else:
                    return self.classify_recursive(record, tree['right'])
        else:
            if(record[tree['split_index']] <= tree['split_value']):
                if(tree['left'] == 0) or (tree['left'] == 1):
                    return tree['left']
                else:
                    return self.classify_recursive(record, tree['left'])
            else:
                if(tree['right'] == 0) or (tree['right'] == 1):
                    return tree['right']
                else:
                    return self.classify_recursive(record, tree['right'])

